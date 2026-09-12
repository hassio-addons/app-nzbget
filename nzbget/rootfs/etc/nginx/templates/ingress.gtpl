server {
    listen {{ .interface }}:{{ .port }} default_server;

    include /etc/nginx/includes/server_params.conf;
    include /etc/nginx/includes/proxy_params.conf;

    # The web interface loads its scripts and its stylesheets as one request
    # each, naming the files in the query string: "combined.js?a.js+b.js".
    # Home Assistant parses the query string into pairs on the way through and
    # writes it back out, and a query without an "=" in it comes back with one
    # appended, so NZBGet is asked for a file called "b.js=" and answers with
    # a 404 page. Taking that "=" off again is all it needs; the "+" survive.
    location ~ ^/combined\.(js|css)$ {
        allow   172.30.32.2;
        deny    all;

        if ($args ~ ^(.*)=$) {
            set $args $1;
        }

        proxy_pass http://backend;
    }

    location / {
        allow   172.30.32.2;
        deny    all;

        # Everything else the web interface asks for is addressed relative to
        # the page it was served from, so it lands on the Ingress path on its
        # own and none of the responses need rewriting.
        proxy_pass http://backend;
    }
}
