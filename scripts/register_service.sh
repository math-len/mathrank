set -e
cp ~/api/scripts/mathrankapi.service /etc/systemd/system/mathrankapi.service
systemctl daemon-reload
