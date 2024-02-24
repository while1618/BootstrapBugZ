/** @type {import('tailwindcss').Config} */
const config = {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  theme: {
    container: {
      center: true,
      padding: '1.25rem',
      screens: {
        xl: '1200px',
        '2xl': '1200px',
      },
    },
    extend: {},
    fontFamily: {
      sans: ['Rowdies', 'sans-serif'],
    },
  },
  plugins: [require('daisyui')],
  daisyui: {
    themes: ['dark', 'cupcake'],
  },
};

module.exports = config;
