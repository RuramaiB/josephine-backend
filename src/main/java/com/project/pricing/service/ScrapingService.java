package com.project.pricing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapingService {

    private final MarketDataService marketDataService;

    public void scrapeAll() {
        scrapeNumbeo(); // Now filtered for grains
        scrapeGMB();
        scrapeZERA();
        scrapeTM();
        scrapeSpar();
        // Disabled per user request
        // scrapeOKZimbabwe();
        // scrapeSpar(); // Spar is now handled separately
        // scrapeChoppies();
    }

    public void scrapeTM() {
        try {
            log.info("Scraping TM Pick n Pay via API...");
            // Using their internal product search API
            // Note: We'll iterate through a few key category slugs
            String[] categories = {"grocery", "butchery", "perishables"};
            int total = 0;
            
            for (String cat : categories) {
                String apiUrl = "https://api.tmpnponline.co.zw/api/v1/product/search-product/" + cat + "/1000/0?page=0";
                Document doc = Jsoup.connect(apiUrl)
                        .ignoreContentType(true)
                        .userAgent("Mozilla/5.0")
                        .get();
                
                String json = doc.text();
                // Basic JSON parsing without a library (using regex for simplicity in this environment)
                Pattern p = Pattern.compile("\\{\"id\":\\d+,\"product_name\":\"(.*?)\",\"slug\":\".*?\",\"price\":(\\d+\\.?\\d*)");
                Matcher m = p.matcher(json);
                
                int count = 0;
                while (m.find() && count < 100) {
                    String name = m.group(1);
                    double price = Double.parseDouble(m.group(2));
                    String category = cat.toUpperCase();
                    marketDataService.trackProductPrice(name, "Generic", category, "Unit", price, "TM Pick n Pay", "Harare");
                    count++;
                    total++;
                }
            }
            log.info("Successfully scraped {} products from TM Pick n Pay API", total);
        } catch (Exception e) {
            log.error("Error scraping TM Pick n Pay API: {}", e.getMessage());
        }
    }

    public void scrapeSpar() {
        scrapeGenericRetailer("Spar Groceries", "https://www.spar.co.zw/products/department/1/groceries", "National", ".product-listing ul li", ".listing-details p", ".product-links strong");
        scrapeGenericRetailer("Spar Butchery", "https://www.spar.co.zw/products/department/7/butchery", "National", ".product-listing ul li", ".listing-details p", ".product-links strong");
    }

    public void scrapeGenericRetailer(String retailer, String url, String region, String containerSel, String nameSel, String priceSel) {
        try {
            log.info("Scraping {} in {} at {}...", retailer, region, url);
            Document doc = Jsoup.connect(url)
                    .sslSocketFactory(socketFactory())
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(20000)
                    .get();

            Elements products = doc.select(containerSel);

            int count = 0;
            for (Element product : products) {
                if (count >= 200) break; // Increased to 200 products per store
                String name = product.select(nameSel).first() != null
                        ? product.select(nameSel).first().text()
                        : "";
                String priceStr = product.select(priceSel).text();

                if (!name.isEmpty() && !priceStr.isEmpty() && priceStr.matches(".*\\d.*")) {
                    double price = parsePrice(priceStr);
                    if (price > 0) {
                        String category = "GROCERY";
                        if (retailer.toLowerCase().contains("butchery")) category = "BUTCHERY";
                        
                        marketDataService.trackProductPrice(name, "Generic", category, "Unit", price, retailer, region);
                        count++;
                    }
                }
            }
            log.info("Successfully scraped {} products from {}", count, retailer);
        } catch (Exception e) {
            log.error("Error scraping {}: {}", retailer, e.getMessage());
        }
    }

    public void scrapeNumbeo() {
        try {
            log.info("Scraping Numbeo for Zimbabwe (Grains Only)...");
            Document doc = Jsoup.connect("https://www.numbeo.com/cost-of-living/country_result.jsp?country=Zimbabwe")
                    .get();
            Elements rows = doc.select("table.data_wide_table tr");

            for (Element row : rows) {
                if (row.select("th").size() > 0) {
                    continue;
                }
                Elements cols = row.select("td");
                if (cols.size() >= 2) {
                    String name = cols.get(0).text();
                    
                    // Filter for grains only
                    String upperName = name.toUpperCase();
                    boolean isGrain = upperName.contains("RICE") || upperName.contains("MAIZE") || 
                                     upperName.contains("WHEAT") || upperName.contains("BEANS") || 
                                     upperName.contains("PEAS") || upperName.contains("BREAD");

                    if (!isGrain) continue;

                    String priceStr = cols.get(1).select(".priceValue").text();
                    if (!priceStr.isEmpty()) {
                        double price = parsePrice(priceStr);
                        marketDataService.trackProductPrice(name, "Reference", "AGRICULTURE", "Standard", price, "Numbeo",
                                "Zimbabwe");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error scraping Numbeo: ", e);
        }
    }

    public void scrapeGMB() {
        try {
            log.info("Scraping GMB for Grain Prices...");
            Document doc = Jsoup.connect("https://gmbdura.co.zw/pricing/").get();
            Elements items = doc.select(".sc_price_item");

            for (Element item : items) {
                String name = item.select(".sc_price_item_title span").text();
                String priceStr = item.select(".sc_price_item_price_value").text();
                if (!priceStr.isEmpty()) {
                    double price = parsePrice(priceStr);
                    marketDataService.trackProductPrice(name, "GMB", "AGRICULTURE", "Tonne", price, "GMB", "National");
                }
            }
        } catch (Exception e) {
            log.error("Error scraping GMB: ", e);
        }
    }

    public void scrapeZERA() {
        try {
            log.info("Scraping ZERA for Fuel Prices...");
            Document doc = Jsoup.connect("https://www.zera.co.zw/").get();
            String text = doc.text();

            // Regex to find Petrol and Diesel prices (updated for ZERA site structure)
            // Example: Petrol Blend (E20) ... $ 2.08
            Pattern pattern = Pattern.compile("(Petrol|Diesel).*?\\$\\s*([0-9.]+)");
            Matcher matcher = pattern.matcher(text);
            
            boolean found = false;
            while (matcher.find()) {
                String type = matcher.group(1);
                double price = Double.parseDouble(matcher.group(2));
                String name = type.contains("Petrol") ? "Petrol Blend (E20)" : "Diesel 50";
                
                marketDataService.trackProductPrice(name, "ZERA", "FUEL", "Litre", price, "ZERA", "National");
                log.info("Recorded ZERA fuel price: {} = {}", name, price);
                found = true;
            }
            
            if (!found) {
                log.warn("Could not find ZERA fuel prices with current regex. Text sample: {}", 
                    text.length() > 500 ? text.substring(0, 500) : text);
            }
        } catch (Exception e) {
            log.error("Error scraping ZERA: ", e);
        }
    }

    private double parsePrice(String priceStr) {
        return Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
    }

    private SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (Exception e) {
            return (SSLSocketFactory) SSLSocketFactory.getDefault();
        }
    }
}
