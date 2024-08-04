package com.dnd.runus.client.vo;

public record OidcPublicKey(String kty, String kid, String use, String alg, String n, String e) {}
