module.exports = {
  extends: ['plugin:cypress/recommended', '<%= relativePathFromE2EToRoot %>/.eslintrc.js'],
  ignorePatterns: ['!**/*'],
  overrides: [
    {
      files: ['*.ts', '*.tsx', '*.js', '*.jsx'],
      extends: ['plugin:@angular-eslint/recommended'],
      parserOptions: {
        project: '<%= e2ePath %>/tsconfig.*?.json',
      },
      rules: {},
    },
    {
      files: ['src/plugins/index.js'],
      rules: {
        '@typescript-eslint/no-var-requires': 'off',
        'no-undef': 'off',
      },
    },
  ],
};
