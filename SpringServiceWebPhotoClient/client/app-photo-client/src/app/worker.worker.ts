/// <reference lib="webworker" />

addEventListener('message', ({ data }) => {
    postMessage(data, null);
});
