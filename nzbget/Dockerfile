ARG BUILD_FROM=ghcr.io/hassio-addons/debian-base:9.4.0
# hadolint ignore=DL3006
FROM ${BUILD_FROM}

# Set shell
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

# Setup base
#
# NZBGet comes from the package upstream publishes, but is unpacked rather than
# installed: the maintainer scripts add a system user and a systemd unit, and
# neither means anything here. Unpacking it over "/" is not the shortcut it
# looks like, since the package carries a /lib directory and that would replace
# the symlink Debian's merged-/usr layout rests on.
ARG BUILD_ARCH=amd64
ARG NZBGET_VERSION=26.3
ARG UNRAR_VERSION=7.1.10
# hadolint ignore=DL3003
RUN \
    apt-get update \
    && apt-get install -y --no-install-recommends \
        build-essential=12.12 \
    \
    && apt-get install -y --no-install-recommends \
        7zip=25.01+dfsg-1~deb13u2 \
        nginx=1.26.3-3+deb13u8 \
        python3=3.13.5-1 \
    \
    && NZBGET_ARCH="${BUILD_ARCH}" \
    && if [[ "${BUILD_ARCH}" = "aarch64" ]]; then NZBGET_ARCH="arm64"; fi \
    \
    && curl -J -L -o /tmp/nzbget.deb \
        "https://github.com/nzbgetcom/nzbget/releases/download/v${NZBGET_VERSION}/nzbget-${NZBGET_VERSION}-${NZBGET_ARCH}.deb" \
    && mkdir -p /tmp/nzbget \
    && dpkg-deb -x /tmp/nzbget.deb /tmp/nzbget \
    && mv /tmp/nzbget/usr/bin/nzbget /usr/bin/nzbget \
    && mv /tmp/nzbget/usr/share/nzbget /usr/share/nzbget \
    \
    && curl -J -L -o /tmp/unrar.tar.gz \
        "https://www.rarlab.com/rar/unrarsrc-${UNRAR_VERSION}.tar.gz" \
    && mkdir -p /tmp/unrar \
    && tar zxf \
        /tmp/unrar.tar.gz \
        --strip 1 -C /tmp/unrar \
    \
    && cd /tmp/unrar \
    && make \
    && install -v -m755 unrar /usr/local/bin \
    && cd - \
    \
    && apt-get purge -y --auto-remove \
        build-essential \
    && apt-get clean \
    && rm -f -r \
        /etc/nginx \
        /tmp/* \
        /var/{cache,log}/* \
        /var/lib/apt/lists/*

# Copy root filesystem
COPY rootfs /

# Build arguments
ARG BUILD_DATE
ARG BUILD_DESCRIPTION
ARG BUILD_NAME
ARG BUILD_REF
ARG BUILD_REPOSITORY
ARG BUILD_VERSION

# Labels
LABEL \
    io.hass.name="${BUILD_NAME}" \
    io.hass.description="${BUILD_DESCRIPTION}" \
    io.hass.arch="${BUILD_ARCH}" \
    io.hass.type="addon" \
    io.hass.version=${BUILD_VERSION} \
    maintainer="Franck Nijhof <opensource@frenck.dev>" \
    org.opencontainers.image.title="${BUILD_NAME}" \
    org.opencontainers.image.description="${BUILD_DESCRIPTION}" \
    org.opencontainers.image.vendor="Home Assistant Community Apps" \
    org.opencontainers.image.authors="Franck Nijhof <opensource@frenck.dev>" \
    org.opencontainers.image.licenses="MIT" \
    org.opencontainers.image.url="https://frenck.dev/home-assistant-apps" \
    org.opencontainers.image.source="https://github.com/${BUILD_REPOSITORY}" \
    org.opencontainers.image.documentation="https://github.com/${BUILD_REPOSITORY}/blob/main/README.md" \
    org.opencontainers.image.created=${BUILD_DATE} \
    org.opencontainers.image.revision=${BUILD_REF} \
    org.opencontainers.image.version=${BUILD_VERSION}
