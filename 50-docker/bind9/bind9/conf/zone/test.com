$TTL 38400
@ IN SOA ns.test.com. admin.test.com. (
2       ;Serial
600     ;Refresh
300     ;Retry
60480   ;Expire
600 )   ;Negative Cache TTL

@       IN      NS      ns.test.com.
@       IN      NS      ns2.test.com.
@       IN      MX  1   127.0.0.1
ns      IN      A       127.0.0.1
ns2     IN      A       127.0.0.2
www     IN      A       8.8.8.8