type ControlFlow = {
  condition: (req: any, res: any) => boolean;
  action: (req: any, res: any) => void;
};

const extractLocation = (req: any) => require('querystring').parse(req._parsedUrl.query).q;

const numbersAreNotAllowedCondition = (req: any, res: any) => new RegExp('.*[0-9]+.*').test(extractLocation(req));
const numbersAreNotAllowedAction = (req: any, res: any) => res.status(400).jsonp({ message: 'Numbers are not allowed' });

const elementNotFoundCondition = (req: any, res: any) => res.locals.data.length === 0;
const elementNotFoundAction = (req: any, res: any) => res.status(404).jsonp({ message: `${extractLocation(req)} was not found` });

const validResponseCondition = (req: any, res: any) => true;
const validResponseAction = (req: any, res: any) => res.jsonp(res.locals.data);

const controlFlows: ControlFlow[] = [
  { condition: numbersAreNotAllowedCondition, action: numbersAreNotAllowedAction },
  { condition: elementNotFoundCondition, action: elementNotFoundAction },
  { condition: validResponseCondition, action: validResponseAction },
];

export const handle = (req: any, res: any) => {
  controlFlows.find((flow) => flow.condition(req, res))!.action(req, res);
};
