import { formatFiles, generateFiles, installPackagesTask, Tree } from '@nrwl/devkit';
import * as path from 'path';
import { wrapAngularDevkitSchematic } from '@nrwl/tao/src/commands/ngcli-adapter';
import { stringUtils } from '@nrwl/workspace';

export const libraryGenerator = wrapAngularDevkitSchematic('@nrwl/angular', 'library');

export default async function (tree: Tree, options: any) {
  await libraryGenerator(tree, options);
  await formatFiles(tree);
  deleteRootEslintFile(tree);
  replaceEslintFileInLib(tree, options);
  return () => {
    installPackagesTask(tree);
  };
}

function deleteRootEslintFile(tree: Tree) {
  tree.delete('.eslintrc.json');
}

function replaceEslintFileInLib(tree: Tree, options: any) {
  const libPath = path.join('libs', stringUtils.dasherize(options.name));
  const filesPath = path.join(__dirname, './files');
  const substitutions = {
    libPath,
    relativePathFromLibToRoot: path.relative(path.dirname(libPath), path.dirname(tree.root)),
  };
  tree.delete(`${libPath}/.eslintrc.json`);
  generateFiles(tree, filesPath, libPath, substitutions);
}
