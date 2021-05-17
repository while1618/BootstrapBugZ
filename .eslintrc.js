module.exports = {
  root: true,
  ignorePatterns: ['**/*'],
  env: {
    es6: true,
  },
  parserOptions: {
    ecmaVersion: 2020,
  },
  overrides: [
    {
      files: ['*.ts', '*.tsx', '*.js', '*.jsx'],
      parserOptions: {
        project: ['tsconfig.base.json'],
        createDefaultProgram: true,
      },
      extends: [
        'plugin:@angular-eslint/recommended',
        'airbnb-typescript/base',
        'prettier',
        'plugin:prettier/recommended',
      ],
      rules: {
        'import/prefer-default-export': 'off',
        'class-methods-use-this': 'off',
        '@typescript-eslint/lines-between-class-members': 'off',
        'no-console': 'error',
        'prefer-destructuring': ['error', { object: true, array: false }],
        'no-underscore-dangle': ['error', { allowAfterThis: true }],
        '@typescript-eslint/no-unused-vars': ['error', { args: 'none' }],
      },
    },
    {
      files: ['*.html'],
      extends: ['plugin:@angular-eslint/template/recommended'],
    },
    {
      files: ['*.component.ts'],
      extends: ['plugin:@angular-eslint/template/process-inline-templates'],
    },
  ],
};
