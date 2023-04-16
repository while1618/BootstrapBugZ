/** @type {import('tailwindcss').Config} */
const config = {
  content: ['./src/**/*.{html,js,svelte,ts}'],
  theme: {
    extend: {},
    fontFamily: {
      sans: ['Rowdies', 'sans-serif'],
    },
  },
  plugins: [require('daisyui')],
};

module.exports = config;
