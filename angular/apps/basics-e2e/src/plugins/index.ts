// eslint-disable-next-line @typescript-eslint/no-var-requires
const webpackPreprocessor = require('@cypress/webpack-preprocessor');
// eslint-disable-next-line @typescript-eslint/no-var-requires
const altWebpackConfig = require('@nrwl/cypress/plugins/preprocessor');
const publicPath = 'http://localhost:4400/';
let outputOptions = {};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
module.exports = (on: (arg0: string, arg1: any) => void, config: any) => {
  const modifiedBaseConfig = altWebpackConfig.getWebpackConfig(config);

  modifiedBaseConfig.module.rules.unshift({
    test: /\.m?js$/,
    loader: 'babel-loader',
    options: {
      plugins: ['@angular/compiler-cli/linker/babel'],
      compact: false,
      cacheDirectory: true,
    },
  });

  Object.defineProperty(modifiedBaseConfig, 'output', {
    get: () => {
      return { ...outputOptions, publicPath };
    },
    set: function (x) {
      outputOptions = x;
    },
  });

  const options = {
    webpackOptions: modifiedBaseConfig,
    typescript: require.resolve('typescript'),
  };

  on('file:preprocessor', webpackPreprocessor(options));

  return config;
};
