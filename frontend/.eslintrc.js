module.exports = {
  root: true,
  ignorePatterns: ['projects/**/*'],
  env: {
    es6: true,
  },
  parserOptions: {
    ecmaVersion: 2022,
  },
  plugins: ['import'],
  overrides: [
    {
      files: ['*.ts', '*.tsx', '*.js', '*.jsx'],
      parserOptions: {
        tsconfigRootDir: __dirname,
        project: ['tsconfig.json'],
        createDefaultProgram: true,
      },
      extends: ['airbnb-typescript/base', 'prettier', 'plugin:prettier/recommended'],
      rules: {
        'import/prefer-default-export': 'off',
        'class-methods-use-this': 'off',
        '@typescript-eslint/lines-between-class-members': 'off',
        'no-console': 'error',
        'prefer-destructuring': ['error', { object: true, array: false }],
        'no-underscore-dangle': ['error', { allowAfterThis: true }],
        '@typescript-eslint/no-unused-vars': ['error', { args: 'none' }],
        'no-plusplus': 'off',
        'import/no-extraneous-dependencies': ['error', { devDependencies: true }],
      },
    },
    {
      files: ['*.ts'],
      extends: [
        'plugin:@angular-eslint/recommended',
        'plugin:@angular-eslint/template/process-inline-templates',
      ],
    },
    {
      files: ['*.html'],
      extends: ['plugin:@angular-eslint/template/recommended'],
      rules: {},
    },
    { files: ['src/main.ts'], rules: { 'no-console': 'off' } },
  ],
};
