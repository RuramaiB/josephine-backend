package com.project.pricing.token;

import com.project.pricing.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Token {

  @Id
  public String id;

  @Indexed(unique = true)
  public String token;

  @Builder.Default
  public TokenType tokenType = TokenType.BEARER;

  public boolean revoked;

  public boolean expired;

  public TokenState tokenState;

  @DBRef
  public User user;
}
