"use strict";
/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
Object.defineProperty(exports, "__esModule", { value: true });
exports.deactivate = exports.activateOri = exports.activate = void 0;
const path = require("path");
const vscode_1 = require("vscode");
const node_1 = require("vscode-languageclient/node");
let client;
function activate(context) {
    // The server is implemented in node
    const serverScriptPath = context.asAbsolutePath(path.join('server', 'bds.sh'));
    const serverOptions = {
        command: serverScriptPath,
        transport: node_1.TransportKind.stdio
    };
    console.log('serverOptions: ' + JSON.stringify(serverOptions));
    // Options to control the language client
    const clientOptions = {
        // Register the server for plain text documents
        documentSelector: [{ scheme: 'file', language: 'plaintext' }],
        synchronize: {
            // Notify the server about file changes to '.clientrc files contained in the workspace
            fileEvents: vscode_1.workspace.createFileSystemWatcher('**/.clientrc')
        }
    };
    console.log('clientOptions: ' + JSON.stringify(clientOptions));
    // Create the language client and start the client.
    client = new node_1.LanguageClient('languageServerExample', 'Language Server Example', serverOptions, clientOptions);
    // Start the client. This will also launch the server
    console.log('Starting language server');
    client.start();
}
exports.activate = activate;
function activateOri(context) {
    // The server is implemented in node
    const serverModule = context.asAbsolutePath(
    // path.join('server', 'out', 'server.js')
    path.join('server', 'bds.sh'));
    console.log('serverModule: ' + serverModule);
    // The debug options for the server
    // --inspect=6009: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging
    const debugOptions = { execArgv: ['--nolazy', '--inspect=6009'] };
    // If the extension is launched in debug mode then the debug server options are used
    // Otherwise the run options are used
    const serverOptions = {
        run: { module: serverModule, transport: node_1.TransportKind.ipc },
        debug: {
            module: serverModule,
            transport: node_1.TransportKind.ipc,
            options: debugOptions
        }
    };
    console.log('serverOptions: ' + JSON.stringify(serverOptions));
    // Options to control the language client
    const clientOptions = {
        // Register the server for plain text documents
        documentSelector: [{ scheme: 'file', language: 'plaintext' }],
        synchronize: {
            // Notify the server about file changes to '.clientrc files contained in the workspace
            fileEvents: vscode_1.workspace.createFileSystemWatcher('**/.clientrc')
        }
    };
    console.log('clientOptions: ' + JSON.stringify(clientOptions));
    // Create the language client and start the client.
    client = new node_1.LanguageClient('languageServerExample', 'Language Server Example', serverOptions, clientOptions);
    // Start the client. This will also launch the server
    console.log('Starting language server');
    client.start();
}
exports.activateOri = activateOri;
function deactivate() {
    if (!client) {
        return undefined;
    }
    console.log('Stopping language server');
    return client.stop();
}
exports.deactivate = deactivate;
//# sourceMappingURL=extension.js.map