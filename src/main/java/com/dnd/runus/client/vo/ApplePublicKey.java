package com.dnd.runus.client.vo;

public record ApplePublicKey(String kty, String kid, String use, String alg, String n, String e) {}
