// TODO: cli script, init idea for file rename

const replace = require('replace-in-file');
const path = require('path');

const options = {
  name: 'blabla',
};

const replaceOptions = {
  files: [
    path.join(__dirname, './../../', '*'),
    path.join(__dirname, './../../apps/', '**/*'),
    path.join(__dirname, './../../libs/', '**/*'),
  ],
  ignore: [
    path.join(__dirname, './../../apps/spring-boot-api', '**/*'),
    path.join(__dirname, './../../libs/spring-boot-api', '**/*'),
    path.join(__dirname, './init.ts'),
  ],
  from: /bootstrapbugz/g,
  to: options.name,
};

replace
  .replaceInFile(replaceOptions)
  .then((results) => {
    console.log(results);
    console.log(`bootstrapbugz replaced with ${options.name}`);
  })
  .catch((error) => {
    console.error('Error occurred:', error);
  });
