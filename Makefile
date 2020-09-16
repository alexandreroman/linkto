all:
	cd backend && pack build alexandreroman/linkto-backend --publish
	cd frontend && pack build alexandreroman/linkto-frontend --publish
