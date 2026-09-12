server {
    listen {{ .interface }}:{{ .port }} default_server;

    include /etc/nginx/includes/server_params.conf;
    include /etc/nginx/includes/proxy_params.conf;

    location / {
        allow   172.30.32.2;
        deny    all;

        # The web interface asks for everything relative to the page it was
        # served from, so it lands on the Ingress path on its own and none of
        # the responses need rewriting.
        proxy_pass http://backend;
    }
}
