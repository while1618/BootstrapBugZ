module.exports = {
  extends: ['../../../../.eslintrc.js'],
  ignorePatterns: ['!**/*'],
  overrides: [
    {
      files: ['*.ts'],
      extends: [
        'plugin:@angular-eslint/recommended',
        'plugin:@nrwl/nx/angular',
        'plugin:@angular-eslint/template/process-inline-templates',
      ],
      parserOptions: { project: ['libs/angular-ui/auth/service/tsconfig.*?.json'] },
      rules: {
        '@angular-eslint/directive-selector': [
          'error',
          { type: 'attribute', prefix: 'bootstrapbugz', style: 'camelCase' },
        ],
        '@angular-eslint/component-selector': [
          'error',
          { type: 'element', prefix: 'bootstrapbugz', style: 'kebab-case' },
        ],
      },
    },
    {
      files: ['*.html'],
      extends: ['plugin:@nrwl/nx/angular-template', 'plugin:@angular-eslint/template/recommended'],
      rules: {},
    },
  ],
};
