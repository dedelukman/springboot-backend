package com.abahstudio.app.core.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieUtil {

    @Value("${jwt.cookie.access-name}")
    private String ACCESS_COOKIE;

    @Value("${jwt.cookie.refresh-name}")
    private String REFRESH_COOKIE;

    @Value("${jwt.cookie.secure}")
    private boolean secure;

    @Value("${jwt.cookie.http-only}")
    private boolean httpOnly;

    @Value("${jwt.cookie.same-site}")
    private String sameSite;

    @Value("${jwt.access.expiration}")
    private int accessExp;

    @Value("${jwt.refresh.expiration}")
    private int refreshExp;

    // ==========================
    //   CREATE COOKIE
    // ==========================

    public void setAccessToken(HttpServletResponse response, String token) {
        Cookie cookie = createCookie(ACCESS_COOKIE, token, accessExp); // 15 menit
        response.addHeader("Set-Cookie", buildCookieHeader(cookie));
    }

    public void setRefreshToken(HttpServletResponse response, String token) {
        Cookie cookie = createCookie(REFRESH_COOKIE, token, refreshExp); // 7 hari
        response.addHeader("Set-Cookie", buildCookieHeader(cookie));
    }

    // ==========================
    //   READ COOKIE
    // ==========================

    public String getAccessToken(HttpServletRequest request) {
        return readCookie(request, ACCESS_COOKIE);
    }

    public String getRefreshToken(HttpServletRequest request) {
        return readCookie(request, REFRESH_COOKIE);
    }

    // ==========================
    //   REMOVE COOKIE
    // ==========================

    public void clearTokens(HttpServletResponse response) {
        deleteCookie(response, ACCESS_COOKIE);
        deleteCookie(response, REFRESH_COOKIE);
    }

    // ==========================
    //   INTERNAL UTILS
    // ==========================

    private Cookie createCookie(String name, String value, int maxAgeSec) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setMaxAge(maxAgeSec);
        return cookie;
    }

    private String buildCookieHeader(Cookie cookie) {
        return cookie.getName() + "=" + cookie.getValue()
                + "; Path=/"
                + "; Max-Age=" + cookie.getMaxAge()
                + (secure ? "; Secure" : "")
                + (httpOnly ? "; HttpOnly" : "")
                + "; SameSite=" + sameSite;
    }

    private String readCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;

        for (Cookie c : req.getCookies()) {
            if (c.getName().equals(name)) return c.getValue();
        }
        return null;
    }

    private void deleteCookie(HttpServletResponse resp, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addHeader("Set-Cookie", buildCookieHeader(cookie));
    }
}
