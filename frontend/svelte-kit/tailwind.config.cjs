/** @type {import('tailwindcss').Config} */
const config = {
  content: [
    './src/**/*.{html,js,svelte,ts}',
    './node_modules/flowbite-svelte/**/*.{html,js,svelte,ts}',
  ],
  theme: {
    extend: {},
    fontFamily: {
      sans: ['Rowdies', 'sans-serif'],
    },
  },
  plugins: [],
  darkMode: 'class',
};

module.exports = config;
