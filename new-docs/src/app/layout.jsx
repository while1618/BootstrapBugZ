import { Footer, Layout, Navbar } from 'nextra-theme-docs';
import 'nextra-theme-docs/style.css';
import { Head } from 'nextra/components';
import { getPageMap } from 'nextra/page-map';

// https://nextjs.org/docs/app/building-your-application/optimizing/metadata
export const metadata = {
  title: {
    default: 'Docs | bugzkit',
    template: '%s | bugzkit',
  },
};

const navbar = <Navbar logo={<b>Nextra</b>} projectLink="https://github.com/while1618/bugzkit" />;
const footer = <Footer>{new Date().getFullYear()} © bugzkit.</Footer>;

export default async function RootLayout({ children }) {
  return (
    <html lang="en" dir="ltr" suppressHydrationWarning>
      <Head></Head>
      <body>
        <Layout
          navbar={navbar}
          pageMap={await getPageMap()}
          docsRepositoryBase="https://github.com/while1618/bugzkit/tree/master/docs"
          footer={footer}
        >
          {children}
        </Layout>
      </body>
    </html>
  );
}
