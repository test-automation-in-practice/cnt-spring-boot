import { handle } from './controllers';

const jsonServer = require('json-server');
const server = jsonServer.create();
const path = require('path');
const router = jsonServer.router(path.join(__dirname, 'db.json'));
const middlewares = jsonServer.defaults();

server.use(middlewares);

router.render = (req: any, res: any) => {
  handle(req, res);
};

server.use(router);
server.listen(4201, () => {
  console.log('JSON Server is running');
});
