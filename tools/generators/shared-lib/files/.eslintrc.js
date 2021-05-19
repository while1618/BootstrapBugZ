module.exports = {
  extends: ['<%= relativePathFromLibToRoot %>/.eslintrc.js'],
  ignorePatterns: ['!**/*'],
  overrides: [
    {
      files: ['*.ts', '*.tsx', '*.js', '*.jsx'],
      parserOptions: { project: ['<%= libPath %>/tsconfig.*?.json'] },
      rules: {},
    },
    {
      files: ['*.ts', '*.tsx'],
      rules: {},
    },
    {
      files: ['*.js', '*.jsx'],
      rules: {},
    },
  ],
};
