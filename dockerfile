FROM nginx

COPY ./ged.conf /etc/nginx/conf.d
COPY . /usr/share/nginx/html