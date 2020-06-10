/*
 * Copyright (c) 2020 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

// Set up Express with static content.
const app = express();
app.use(express.static(__dirname + '/static'));

// Set up a liveness probe (used by platforms to monitor this process).
app.get('/health', function(req, res) {
  res.json({status: 'UP'});
});

// Forward API requests to the backend.
const backendHost = process.env.BACKEND_HOST || 'localhost';
const backendPort = process.env.BACKEND_PORT || 8081;
const backendUrl = `http://${backendHost}:${backendPort}`;
const backendProxy = createProxyMiddleware(backendUrl);
app.use('/api/**', backendProxy);
app.use('/go/**', backendProxy);

// Start the web server.
const port = process.env.PORT || 8080;
app.listen(port, () => {
  console.log(`Server listening on port ${port}`);
});
