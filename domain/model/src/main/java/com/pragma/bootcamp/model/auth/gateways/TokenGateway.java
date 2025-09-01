package com.pragma.bootcamp.model.auth.gateways;

import com.pragma.bootcamp.model.user.User;

public interface TokenGateway {
    String generateToken(User user);
}
