import { Tree, formatFiles, installPackagesTask, generateFiles } from '@nrwl/devkit';
import * as path from 'path';
import { wrapAngularDevkitSchematic } from '@nrwl/tao/src/commands/ngcli-adapter';
import { stringUtils } from '@nrwl/workspace';

export const libraryGenerator = wrapAngularDevkitSchematic('@nrwl/angular', 'app');

export default async function (tree: Tree, options: any) {
  await libraryGenerator(tree, options);
  await formatFiles(tree);
  deleteRootEslintFile(tree);
  replaceEslintFileInApp(tree, options);
  replaceEslintFileInE2E(tree, options);
  return () => {
    installPackagesTask(tree);
  };
}

function deleteRootEslintFile(tree: Tree) {
  tree.delete('.eslintrc.json');
}

function replaceEslintFileInApp(tree: Tree, options: any) {
  const appPath = path.join('apps', stringUtils.dasherize(options.name));
  const filesPath = path.join(__dirname, './files/app');
  const substitutions = {
    appPath,
    relativePathFromAppToRoot: path.relative(path.dirname(appPath), path.dirname(tree.root)),
  };
  tree.delete(`${appPath}/.eslintrc.json`);
  generateFiles(tree, filesPath, appPath, substitutions);
}

function replaceEslintFileInE2E(tree: Tree, options: any) {
  const e2ePath = path.join('apps', `${stringUtils.dasherize(options.name)}-e2e`);
  const filesPath = path.join(__dirname, './files/e2e');
  const substitutions = {
    e2ePath,
    relativePathFromE2EToRoot: path.relative(path.dirname(e2ePath), path.dirname(tree.root)),
  };
  tree.delete(`${e2ePath}/.eslintrc.json`);
  generateFiles(tree, filesPath, e2ePath, substitutions);
}
