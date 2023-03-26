package com.jolyn.meetingroomreservationsystem.constant;

public class AuthorityConstant {
    public static final String[] USER_AUTHORITIES = {"reservation:read","reservation:update","reservation:delete","reservation:create"};
    public static final String[] ADMIN_AUTHORITIES = {"user:read","user:update","user:create","user:delete","reservation:read","reservation:update","reservation:delete","reservation:create"};
}
