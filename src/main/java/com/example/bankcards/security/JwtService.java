package com.example.bankcards.security;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.bankcards.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {
    
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @Value("${token.expiration.access}")
    private long accessTokenExpiration;

     /**
     * Извлекает имя пользователя (subject) из JWT токена
     *
     * @param token JWT токен
     * @return имя пользователя (username)
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Генерирует JWT токен для пользователя
     *
     * @param userDetails данные пользователя
     * @return сгенерированный JWT токен
     * <p>Дополнительно включает в токен следующие claims:
     * <ul>
     *   <li>id - идентификатор пользователя</li>
     *   <li>phone - номер телефона пользователя</li>
     *   <li>role - роль пользователя</li>
     * </ul>
     * </p>
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("phone", customUserDetails.getPhoneNumber());
            claims.put("role", customUserDetails.getRole());
        }
        return generateToken(claims, userDetails);
    }

     /**
     * Проверяет валидность токена для указанного пользователя
     *
     * @param token JWT токен для проверки
     * @param userDetails данные пользователя для сравнения
     * @return true если токен валиден и принадлежит пользователю, иначе false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }


    /**
     * Извлекает конкретное значение (claim) из токена
     * 
     * @param <T> тип возвращаемого значения
     * @param token JWT токен
     * @param claimsResolvers функция для извлечения конкретного claim
     * @return значение запрошенного claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }


      /**
     * Генерирует JWT токен с дополнительными claims
     *
     * @param extraClaims дополнительные данные для включения в токен
     * @param userDetails данные пользователя
     * @return сгенерированный JWT токен
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claims(extraClaims)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(getSigningKey() , Jwts.SIG.HS256).compact();
          
    }



    /**
     * Извлекает все claims из токена
     *
     * @param token JWT токен
     * @return объект Claims со всеми данными токена
     */
    private Claims extractAllClaims(String token) {
         return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }


    /**
     * Проверяет истек ли срок действия токена
     *
     * @param token JWT токен
     * @return true если токен просрочен, иначе false
     * @throws JwtException если токен невалиден или не может быть обработан
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new java.util.Date());
    }

    /**
     * Извлекает дату истечения срока действия токена
     *
     * @param token JWT токен
     * @return дата истечения срока действия
     * @throws JwtException если токен невалиден или не может быть обработан
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


   
    /**
     * Создает ключ для подписи токенов на основе конфигурации
     *
     * @return секретный ключ для подписи JWT
     * @throws IllegalArgumentException если ключ подписи невалиден
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
