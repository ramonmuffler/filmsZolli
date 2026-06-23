const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers ?? {}),
    },
    ...options,
  });

  if (!response.ok) {
    const detail = await response.json().catch(() => ({}));
    throw new Error(detail.message ?? `HTTP ${response.status}`);
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export const api = {
  list: (path) => request(path),
  create: (path, body) =>
    request(path, {
      method: "POST",
      body: JSON.stringify(body),
    }),
  update: (path, id, body) =>
    request(`${path}/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    }),
  remove: (path, id) =>
    request(`${path}/${id}`, {
      method: "DELETE",
    }),
};
