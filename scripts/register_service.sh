set -e
cp /home/ubuntu/api/scripts/mathrankapi.service /etc/systemd/system/mathrankapi.service
systemctl daemon-reload
