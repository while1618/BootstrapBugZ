module.exports = {
  extends: ['<%= relativePathFromAppToRoot %>/.eslintrc.js'],
  ignorePatterns: ['!**/*'],
  overrides: [
    {
      files: ['*.ts'],
      extends: [
        'plugin:@angular-eslint/recommended',
        'plugin:@nrwl/nx/angular',
        'plugin:@angular-eslint/template/process-inline-templates',
      ],
      parserOptions: { project: ['<%= appPath %>/tsconfig.*?.json'] },
    },
    {
      files: ['*.html'],
      extends: ['plugin:@nrwl/nx/angular-template', 'plugin:@angular-eslint/template/recommended'],
      rules: {},
    },
    { files: ['src/main.ts'], rules: { 'no-console': 'off' } },
    {
      files: ['src/test-setup.ts'],
      rules: { 'import/no-extraneous-dependencies': 'off' },
    },
  ],
};
