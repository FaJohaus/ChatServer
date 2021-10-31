docker network create \
--driver=bridge \
--subnet=10.20.0.0/16 \
--ip-range=10.20.0.0/24 \
--gateway=10.20.0.254 \
mso_rooms

docker compose up 