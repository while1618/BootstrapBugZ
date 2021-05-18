import {
  formatFiles,
  generateFiles,
  installPackagesTask,
  joinPathFragments,
  Tree,
} from '@nrwl/devkit';
import * as path from 'path';
import { wrapAngularDevkitSchematic } from '@nrwl/tao/src/commands/ngcli-adapter';

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
  const libPath = `libs/${options.name}`;
  const filesPath = joinPathFragments(__dirname, './files');
  tree.delete(`${libPath}/.eslintrc.json`);
  const substitutions = {
    libPath,
    relativePathFromLibToRoot: path.relative(path.dirname(libPath), path.dirname(tree.root)),
  };
  generateFiles(tree, filesPath, libPath, substitutions);
}
