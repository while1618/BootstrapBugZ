module.exports = {
  extends: ['../../.eslintrc.js'],
  ignorePatterns: ['!**/*'],
  overrides: [
    {
      files: ['*.ts'],
      extends: [
        'plugin:@angular-eslint/recommended',
        'plugin:@nrwl/nx/angular',
        'plugin:@angular-eslint/template/process-inline-templates',
      ],
      parserOptions: { project: ['libs/angular-ui/admin/data/tsconfig.*?.json'] },
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
