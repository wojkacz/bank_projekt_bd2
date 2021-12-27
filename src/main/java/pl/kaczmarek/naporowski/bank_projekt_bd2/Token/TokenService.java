package pl.kaczmarek.naporowski.bank_projekt_bd2.Token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TokenService {

    TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateTokenString(){
        List<Token> tokens = tokenRepository.findAll();
        String letters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789";
        Random random = new Random();
        String tokenStr;
        boolean isGood;

        do {
            isGood = true;
            tokenStr = "";

            for (int i = 0; i < 32; i++)
                tokenStr += letters.charAt(random.nextInt(letters.length()));

            for (Token token : tokens)
                if(token.getToken().equals(tokenStr)) {
                    isGood = false;
                    break;
                }
        } while(!isGood);
        return tokenStr;
    }

    public String newToken(Long user_id){
        Optional<Token> tokenOptional = tokenRepository.findTokenByUserID(user_id);
        if(tokenOptional.isPresent()) {
            String tokenStr = tokenOptional.get().getToken();
            refreshTokenTime(tokenStr);
            return tokenStr;
        }

        Token token = new Token(user_id);
        String tokenStr = generateTokenString();
        token.setToken(tokenStr);
        tokenRepository.save(token);
        return tokenStr;
    }

    public int refreshTokenTime(String tokenStr){
        Optional<Token> tokenOptional = tokenRepository.findTokenByStr(tokenStr);
        if(tokenOptional.isPresent()) {
            Token token = tokenOptional.get();
            token.setExpiration_time(LocalDateTime.now().plusMinutes(15));
            tokenRepository.save(token);
            return 0;
        }
        System.out.println("Token does not exist!");
        return 1;
    }

    public void verifyTokens(){
        List<Token> tokens = tokenRepository.findAll();
        for(Token token : tokens)
            if(LocalDateTime.now().isAfter(token.getExpiration_time()))
                tokenRepository.delete(token);
    }

    public Long getUserIdFromToken(String tokenStr){
        List<Token> tokens = tokenRepository.findAll();
        for(Token token : tokens)
            if(token.getToken().equals(tokenStr))
                return token.getUser_id();
        return null;
    }
}
