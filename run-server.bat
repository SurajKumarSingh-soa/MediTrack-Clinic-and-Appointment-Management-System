@echo off
echo ========================================
echo   Running MediTrack REST API Server
echo ========================================
echo.

java -cp bin com.airtribe.meditrack.api.MediTrackServer %*
