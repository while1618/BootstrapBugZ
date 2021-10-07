module.exports = {
  root: true,
  ignorePatterns: ['**/*'],
  env: {
    es6: true,
  },
  parserOptions: {
    ecmaVersion: 2020,
  },
  plugins: ['@nrwl/nx', 'import'],
  overrides: [
    {
      files: ['*.ts', '*.tsx', '*.js', '*.jsx'],
      parserOptions: {
        project: ['tsconfig.base.json'],
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
        '@nrwl/nx/enforce-module-boundaries': [
          'error',
          {
            enforceBuildableLibDependency: true,
            allow: [],
            depConstraints: [{ sourceTag: '*', onlyDependOnLibsWithTags: ['*'] }],
          },
        ],
      },
    },
    {
      files: ['*.ts', '*.tsx'],
      extends: ['plugin:@nrwl/nx/typescript'],
    },
    {
      files: ['*.js', '*.jsx'],
      extends: ['plugin:@nrwl/nx/javascript'],
    },
  ],
};
