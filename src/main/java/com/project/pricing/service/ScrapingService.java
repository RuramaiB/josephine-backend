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
        scrapeNumbeo();
        scrapeGMB();
        scrapeZERA();
        scrapeTM();
        scrapeOKZimbabwe();
        scrapeSpar();
        scrapeChoppies();
    }

    public void scrapeTM() {
        scrapeGenericRetailer("TM Pick n Pay", "https://tmpnponline.co.zw/shop/", "Harare");
    }

    public void scrapeOKZimbabwe() {
        scrapeGenericRetailer("OK Zimbabwe", "https://okzim.co.zw/product-category/groceries/", "National");
    }

    public void scrapeSpar() {
        scrapeGenericRetailer("Spar Zimbabwe", "https://spar.co.zw/shop/", "National");
    }

    public void scrapeChoppies() {
        scrapeGenericRetailer("Choppies", "https://choppies.co.zw/product-category/grocery/", "National");
    }

    public void scrapeGenericRetailer(String retailer, String url, String region) {
        try {
            log.info("Scraping {} in {} at {}...", retailer, region, url);
            Document doc = Jsoup.connect(url)
                    .sslSocketFactory(socketFactory())
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(15000)
                    .get();

            Elements products = doc.select(".product, .product-item, .item-product, .entry-product, .product-miniature, .list-product");

            int count = 0;
            for (Element product : products) {
                if (count >= 120) break; // Increased to 120 products per store
                String name = product.select(".woocommerce-loop-product__title, .product-title, .name, h2, h3")
                        .first() != null
                                ? product.select(".woocommerce-loop-product__title, .product-title, .name, h2, h3")
                                        .first()
                                        .text()
                                : "";
                String priceStr = product.select(".price, .product-price, .amount, .price-current").text();

                if (!name.isEmpty() && !priceStr.isEmpty() && priceStr.matches(".*\\d.*")) {
                    double price = parsePrice(priceStr);
                    if (price > 0) {
                        marketDataService.trackProductPrice(name, "Generic", "RETAIL", "Unit", price, retailer, region);
                        count++;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error scraping {}: {}", retailer, e.getMessage());
        }
    }

    public void scrapeNumbeo() {
        try {
            log.info("Scraping Numbeo for Zimbabwe...");
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
                    // Skip if not essential (Logic handled in marketDataService, but check here for efficiency)
                    String priceStr = cols.get(1).select(".priceValue").text();
                    if (!priceStr.isEmpty()) {
                        double price = parsePrice(priceStr);
                        marketDataService.trackProductPrice(name, "Reference", "GROCERY", "Standard", price, "Numbeo",
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

            // Regex to find Petrol and Diesel prices
            Pattern pattern = Pattern.compile("Petrol.*?\\$\\s*([0-9.]+)|Diesel.*?\\$\\s*([0-9.]+)");
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    marketDataService.trackProductPrice("Petrol Blend (E5)", "ZERA", "FUEL", "Litre",
                            Double.parseDouble(matcher.group(1)), "ZERA", "National");
                }
                if (matcher.group(2) != null) {
                    marketDataService.trackProductPrice("Diesel 50", "ZERA", "FUEL", "Litre",
                            Double.parseDouble(matcher.group(2)), "ZERA", "National");
                }
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
