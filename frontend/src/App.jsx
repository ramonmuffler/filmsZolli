import { useEffect, useMemo, useState } from "react";
import {
  Edit3,
  Eye,
  Film,
  LogIn,
  LogOut,
  Plus,
  RefreshCw,
  Save,
  Star,
  Tags,
  Trash2,
  UserPlus,
  X,
} from "lucide-react";
import { api } from "./api";

const endpoints = {
  contents: "/api/contents",
  categories: "/api/categories",
  users: "/api/users",
  ratings: "/api/ratings",
  watched: "/api/watched",
};

const tabs = [
  { id: "contents", label: "Inhalte", icon: Film },
  { id: "categories", label: "Kategorien", icon: Tags },
  { id: "ratings", label: "Bewerten", icon: Star },
  { id: "watched", label: "Gesehen", icon: Eye },
];

const nowLocal = () => {
  const date = new Date();
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 16);
};

const toLocalInput = (value) => {
  if (!value) {
    return nowLocal();
  }
  const date = new Date(value);
  date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
  return date.toISOString().slice(0, 16);
};

const toIso = (value) => (value ? new Date(value).toISOString() : null);

const defaultForms = () => ({
  content: {
    title: "",
    description: "",
    releaseYear: new Date().getFullYear(),
    type: "FILM",
    categoryIds: [],
  },
  category: {
    name: "",
  },
  auth: {
    username: "",
    email: "",
    password: "",
    usernameOrEmail: "",
  },
  rating: {
    contentId: "",
    stars: 5,
    comment: "",
    ratingDate: nowLocal(),
  },
  watched: {
    contentId: "",
    watchedDate: nowLocal(),
  },
});

function App() {
  const [activeTab, setActiveTab] = useState("contents");
  const [authMode, setAuthMode] = useState("login");
  const [currentUser, setCurrentUser] = useState(() => {
    const stored = localStorage.getItem("films-current-user");
    return stored ? JSON.parse(stored) : null;
  });
  const [data, setData] = useState({
    contents: [],
    categories: [],
    users: [],
    ratings: [],
    watched: [],
  });
  const [forms, setForms] = useState(defaultForms);
  const [editing, setEditing] = useState({});
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState("");
  const [error, setError] = useState("");

  const categoryMap = useMemo(
    () => new Map(data.categories.map((category) => [category.id, category.name])),
    [data.categories]
  );
  const userMap = useMemo(
    () => new Map(data.users.map((user) => [user.id, user.username])),
    [data.users]
  );
  const contentMap = useMemo(
    () => new Map(data.contents.map((content) => [content.id, content.title])),
    [data.contents]
  );

  useEffect(() => {
    loadAll();
  }, []);

  async function loadAll() {
    setLoading(true);
    setError("");
    try {
      const entries = await Promise.all(
        Object.entries(endpoints).map(async ([key, path]) => [key, await api.list(path)])
      );
      setData(Object.fromEntries(entries));
      setStatus("Aktualisiert");
    } catch (exception) {
      setError(exception.message);
      setStatus("API nicht erreichbar");
    } finally {
      setLoading(false);
    }
  }

  function updateForm(formName, patch) {
    setForms((current) => ({
      ...current,
      [formName]: {
        ...current[formName],
        ...patch,
      },
    }));
  }

  function resetForm(formName) {
    setForms((current) => ({
      ...current,
      [formName]: defaultForms()[formName],
    }));
    setEditing((current) => ({
      ...current,
      [formName]: null,
    }));
  }

  async function saveItem(event, formName, collectionName, body) {
    event.preventDefault();
    setError("");
    try {
      if (editing[formName]) {
        await api.update(endpoints[collectionName], editing[formName], body);
      } else {
        await api.create(endpoints[collectionName], body);
      }
      resetForm(formName);
      await loadAll();
    } catch (exception) {
      setError(exception.message);
    }
  }

  async function submitAuth(event) {
    event.preventDefault();
    setError("");
    try {
      const path = authMode === "login" ? "/api/auth/login" : "/api/auth/register";
      const body =
        authMode === "login"
          ? {
              usernameOrEmail: forms.auth.usernameOrEmail,
              password: forms.auth.password,
            }
          : {
              username: forms.auth.username,
              email: forms.auth.email,
              password: forms.auth.password,
            };
      const user = await api.create(path, body);
      setCurrentUser(user);
      localStorage.setItem("films-current-user", JSON.stringify(user));
      resetForm("auth");
      setStatus(authMode === "login" ? "Eingeloggt" : "Registriert");
      await loadAll();
    } catch (exception) {
      setError(exception.message);
    }
  }

  function logout() {
    setCurrentUser(null);
    localStorage.removeItem("films-current-user");
    setStatus("Ausgeloggt");
  }

  async function removeItem(collectionName, id) {
    if (!window.confirm("Diesen Eintrag loeschen?")) {
      return;
    }
    setError("");
    try {
      await api.remove(endpoints[collectionName], id);
      await loadAll();
    } catch (exception) {
      setError(exception.message);
    }
  }

  function editContent(content) {
    setActiveTab("contents");
    setEditing((current) => ({ ...current, content: content.id }));
    updateForm("content", {
      title: content.title ?? "",
      description: content.description ?? "",
      releaseYear: content.releaseYear ?? new Date().getFullYear(),
      type: content.type ?? "FILM",
      categoryIds: content.categoryIds ?? [],
    });
  }

  function editCategory(category) {
    setActiveTab("categories");
    setEditing((current) => ({ ...current, category: category.id }));
    updateForm("category", { name: category.name ?? "" });
  }

  function editRating(rating) {
    setActiveTab("ratings");
    setEditing((current) => ({ ...current, rating: rating.id }));
    updateForm("rating", {
      contentId: rating.contentId ?? "",
      stars: rating.stars ?? 5,
      comment: rating.comment ?? "",
      ratingDate: toLocalInput(rating.ratingDate),
    });
  }

  function editWatched(watched) {
    setActiveTab("watched");
    setEditing((current) => ({ ...current, watched: watched.id }));
    updateForm("watched", {
      contentId: watched.contentId ?? "",
      watchedDate: toLocalInput(watched.watchedDate),
    });
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Filme & Serien</p>
          <h1>Films</h1>
        </div>
        <div className="topbar-actions">
          <span className={error ? "status status-error" : "status"}>{error || status}</span>
          <button className="icon-button" type="button" onClick={loadAll} title="Aktualisieren">
            <RefreshCw size={18} />
          </button>
        </div>
      </header>

      <section className="auth-strip">
        {currentUser ? (
          <div className="signed-in">
            <div>
              <span className="auth-label">Angemeldet als</span>
              <strong>{currentUser.username}</strong>
            </div>
            <button className="secondary-button" type="button" onClick={logout}>
              <LogOut size={18} />
              <span>Logout</span>
            </button>
          </div>
        ) : (
          <form className="auth-form" onSubmit={submitAuth}>
            <div className="auth-toggle" role="group" aria-label="Authentifizierung">
              <button
                className={authMode === "login" ? "toggle-active" : ""}
                type="button"
                onClick={() => setAuthMode("login")}
              >
                <LogIn size={17} />
                <span>Login</span>
              </button>
              <button
                className={authMode === "register" ? "toggle-active" : ""}
                type="button"
                onClick={() => setAuthMode("register")}
              >
                <UserPlus size={17} />
                <span>Registrieren</span>
              </button>
            </div>
            {authMode === "register" && (
              <>
                <label>
                  Username
                  <input
                    required
                    value={forms.auth.username}
                    onChange={(event) => updateForm("auth", { username: event.target.value })}
                  />
                </label>
                <label>
                  E-Mail
                  <input
                    required
                    type="email"
                    value={forms.auth.email}
                    onChange={(event) => updateForm("auth", { email: event.target.value })}
                  />
                </label>
              </>
            )}
            {authMode === "login" && (
              <label>
                Username oder E-Mail
                <input
                  required
                  value={forms.auth.usernameOrEmail}
                  onChange={(event) => updateForm("auth", { usernameOrEmail: event.target.value })}
                />
              </label>
            )}
            <label>
              Passwort
              <input
                required
                minLength="6"
                type="password"
                value={forms.auth.password}
                onChange={(event) => updateForm("auth", { password: event.target.value })}
              />
            </label>
            <button className="primary-button" type="submit">
              {authMode === "login" ? <LogIn size={18} /> : <UserPlus size={18} />}
              <span>{authMode === "login" ? "Einloggen" : "Account erstellen"}</span>
            </button>
          </form>
        )}
      </section>

      <nav className="tabs" aria-label="Bereiche">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          return (
            <button
              className={activeTab === tab.id ? "tab tab-active" : "tab"}
              key={tab.id}
              type="button"
              onClick={() => setActiveTab(tab.id)}
            >
              <Icon size={18} />
              <span>{tab.label}</span>
            </button>
          );
        })}
      </nav>

      {loading && <div className="loading-bar" />}

      {activeTab === "contents" && (
        <section className="workspace">
          <form
            className="panel editor"
            onSubmit={(event) =>
              saveItem(event, "content", "contents", {
                ...forms.content,
                releaseYear: Number(forms.content.releaseYear),
              })
            }
          >
            <PanelHeader
              icon={Film}
              title={editing.content ? "Inhalt bearbeiten" : "Inhalt erfassen"}
              editing={editing.content}
              onCancel={() => resetForm("content")}
            />
            <div className="form-grid">
              <label>
                Titel
                <input
                  required
                  value={forms.content.title}
                  onChange={(event) => updateForm("content", { title: event.target.value })}
                />
              </label>
              <label>
                Jahr
                <input
                  required
                  min="1888"
                  max="2100"
                  type="number"
                  value={forms.content.releaseYear}
                  onChange={(event) => updateForm("content", { releaseYear: event.target.value })}
                />
              </label>
              <label>
                Typ
                <select
                  value={forms.content.type}
                  onChange={(event) => updateForm("content", { type: event.target.value })}
                >
                  <option value="FILM">Film</option>
                  <option value="SERIE">Serie</option>
                </select>
              </label>
              <label className="span-2">
                Beschreibung
                <textarea
                  rows="4"
                  value={forms.content.description}
                  onChange={(event) => updateForm("content", { description: event.target.value })}
                />
              </label>
            </div>
            <div className="check-group">
              {data.categories.length === 0 && <p className="muted">Keine Kategorien vorhanden</p>}
              {data.categories.map((category) => (
                <label className="check-row" key={category.id}>
                  <input
                    type="checkbox"
                    checked={forms.content.categoryIds.includes(category.id)}
                    onChange={(event) => {
                      const categoryIds = event.target.checked
                        ? [...forms.content.categoryIds, category.id]
                        : forms.content.categoryIds.filter((id) => id !== category.id);
                      updateForm("content", { categoryIds });
                    }}
                  />
                  <span>{category.name}</span>
                </label>
              ))}
            </div>
            <ActionButton editing={editing.content} />
          </form>

          <div className="panel table-panel">
            <TableTitle title="Inhalte" count={data.contents.length} />
            <table>
              <thead>
                <tr>
                  <th>Titel</th>
                  <th>Typ</th>
                  <th>Jahr</th>
                  <th>Kategorien</th>
                  <th>Rating</th>
                  <th aria-label="Aktionen" />
                </tr>
              </thead>
              <tbody>
                {data.contents.map((content) => (
                  <tr key={content.id}>
                    <td>
                      <strong>{content.title}</strong>
                      <span className="subtext">{content.description}</span>
                    </td>
                    <td>{content.type === "SERIE" ? "Serie" : "Film"}</td>
                    <td>{content.releaseYear}</td>
                    <td>
                      <TokenList
                        values={(content.categoryIds ?? []).map((id) => categoryMap.get(id) ?? id)}
                      />
                    </td>
                    <td>{Number(content.averageRating ?? 0).toFixed(1)}</td>
                    <RowActions
                      onEdit={() => editContent(content)}
                      onDelete={() => removeItem("contents", content.id)}
                    />
                  </tr>
                ))}
                <EmptyRow visible={data.contents.length === 0} columns={6} label="Noch keine Inhalte" />
              </tbody>
            </table>
          </div>
        </section>
      )}

      {activeTab === "categories" && (
        <section className="workspace">
          <form
            className="panel editor"
            onSubmit={(event) => saveItem(event, "category", "categories", forms.category)}
          >
            <PanelHeader
              icon={Tags}
              title={editing.category ? "Kategorie bearbeiten" : "Kategorie erstellen"}
              editing={editing.category}
              onCancel={() => resetForm("category")}
            />
            <label>
              Name
              <input
                required
                value={forms.category.name}
                onChange={(event) => updateForm("category", { name: event.target.value })}
              />
            </label>
            <ActionButton editing={editing.category} />
          </form>

          <div className="panel table-panel">
            <TableTitle title="Kategorien" count={data.categories.length} />
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Verwendet</th>
                  <th aria-label="Aktionen" />
                </tr>
              </thead>
              <tbody>
                {data.categories.map((category) => (
                  <tr key={category.id}>
                    <td>
                      <strong>{category.name}</strong>
                    </td>
                    <td>
                      {
                        data.contents.filter((content) =>
                          (content.categoryIds ?? []).includes(category.id)
                        ).length
                      }
                    </td>
                    <RowActions
                      onEdit={() => editCategory(category)}
                      onDelete={() => removeItem("categories", category.id)}
                    />
                  </tr>
                ))}
                <EmptyRow visible={data.categories.length === 0} columns={3} label="Noch keine Kategorien" />
              </tbody>
            </table>
          </div>
        </section>
      )}

      {activeTab === "ratings" && (
        <section className="workspace">
          <form
            className="panel editor"
            onSubmit={(event) =>
              saveItem(event, "rating", "ratings", {
                ...forms.rating,
                userId: currentUser?.id,
                stars: Number(forms.rating.stars),
                ratingDate: toIso(forms.rating.ratingDate),
              })
            }
          >
            <PanelHeader
              icon={Star}
              title={editing.rating ? "Bewertung bearbeiten" : "Bewertung speichern"}
              editing={editing.rating}
              onCancel={() => resetForm("rating")}
            />
            {!currentUser && <p className="auth-note">Bitte einloggen, um Bewertungen zu speichern.</p>}
            <div className="form-grid">
              <label>
                Inhalt
                <select
                  required
                  value={forms.rating.contentId}
                  onChange={(event) => updateForm("rating", { contentId: event.target.value })}
                >
                  <option value="">Auswahl</option>
                  {data.contents.map((content) => (
                    <option key={content.id} value={content.id}>
                      {content.title}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Sterne
                <input
                  required
                  type="number"
                  min="1"
                  max="5"
                  value={forms.rating.stars}
                  onChange={(event) => updateForm("rating", { stars: event.target.value })}
                />
              </label>
              <label>
                Datum
                <input
                  type="datetime-local"
                  value={forms.rating.ratingDate}
                  onChange={(event) => updateForm("rating", { ratingDate: event.target.value })}
                />
              </label>
              <label className="span-2">
                Kommentar
                <textarea
                  rows="4"
                  value={forms.rating.comment}
                  onChange={(event) => updateForm("rating", { comment: event.target.value })}
                />
              </label>
            </div>
            <ActionButton editing={editing.rating} disabled={!currentUser || !data.contents.length} />
          </form>

          <div className="panel table-panel">
            <TableTitle title="Bewertungen" count={data.ratings.length} />
            <table>
              <thead>
                <tr>
                  <th>Inhalt</th>
                  <th>Benutzer</th>
                  <th>Sterne</th>
                  <th>Kommentar</th>
                  <th aria-label="Aktionen" />
                </tr>
              </thead>
              <tbody>
                {data.ratings.map((rating) => (
                  <tr key={rating.id}>
                    <td>{contentMap.get(rating.contentId) ?? rating.contentId}</td>
                    <td>{userMap.get(rating.userId) ?? rating.userId}</td>
                    <td>{rating.stars}</td>
                    <td>{rating.comment}</td>
                    <RowActions
                      onEdit={() => editRating(rating)}
                      onDelete={() => removeItem("ratings", rating.id)}
                    />
                  </tr>
                ))}
                <EmptyRow visible={data.ratings.length === 0} columns={5} label="Noch keine Bewertungen" />
              </tbody>
            </table>
          </div>
        </section>
      )}

      {activeTab === "watched" && (
        <section className="workspace">
          <form
            className="panel editor"
            onSubmit={(event) =>
              saveItem(event, "watched", "watched", {
                ...forms.watched,
                userId: currentUser?.id,
                watchedDate: toIso(forms.watched.watchedDate),
              })
            }
          >
            <PanelHeader
              icon={Eye}
              title={editing.watched ? "Gesehen bearbeiten" : "Als gesehen markieren"}
              editing={editing.watched}
              onCancel={() => resetForm("watched")}
            />
            {!currentUser && <p className="auth-note">Bitte einloggen, um Inhalte als gesehen zu markieren.</p>}
            <div className="form-grid">
              <label>
                Inhalt
                <select
                  required
                  value={forms.watched.contentId}
                  onChange={(event) => updateForm("watched", { contentId: event.target.value })}
                >
                  <option value="">Auswahl</option>
                  {data.contents.map((content) => (
                    <option key={content.id} value={content.id}>
                      {content.title}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Datum
                <input
                  type="datetime-local"
                  value={forms.watched.watchedDate}
                  onChange={(event) => updateForm("watched", { watchedDate: event.target.value })}
                />
              </label>
            </div>
            <ActionButton editing={editing.watched} disabled={!currentUser || !data.contents.length} />
          </form>

          <div className="panel table-panel">
            <TableTitle title="Gesehene Inhalte" count={data.watched.length} />
            <table>
              <thead>
                <tr>
                  <th>Inhalt</th>
                  <th>Benutzer</th>
                  <th>Datum</th>
                  <th aria-label="Aktionen" />
                </tr>
              </thead>
              <tbody>
                {data.watched.map((watched) => (
                  <tr key={watched.id}>
                    <td>{contentMap.get(watched.contentId) ?? watched.contentId}</td>
                    <td>{userMap.get(watched.userId) ?? watched.userId}</td>
                    <td>{formatDate(watched.watchedDate)}</td>
                    <RowActions
                      onEdit={() => editWatched(watched)}
                      onDelete={() => removeItem("watched", watched.id)}
                    />
                  </tr>
                ))}
                <EmptyRow visible={data.watched.length === 0} columns={4} label="Noch keine Eintraege" />
              </tbody>
            </table>
          </div>
        </section>
      )}
    </main>
  );
}

function PanelHeader({ icon: Icon, title, editing, onCancel }) {
  return (
    <div className="panel-header">
      <div className="panel-title">
        <Icon size={19} />
        <h2>{title}</h2>
      </div>
      {editing && (
        <button className="icon-button" type="button" onClick={onCancel} title="Abbrechen">
          <X size={18} />
        </button>
      )}
    </div>
  );
}

function TableTitle({ title, count }) {
  return (
    <div className="table-title">
      <h2>{title}</h2>
      <span>{count}</span>
    </div>
  );
}

function ActionButton({ editing, disabled = false }) {
  return (
    <button className="primary-button" type="submit" disabled={disabled}>
      {editing ? <Save size={18} /> : <Plus size={18} />}
      <span>{editing ? "Speichern" : "Erstellen"}</span>
    </button>
  );
}

function RowActions({ onEdit, onDelete }) {
  return (
    <td className="row-actions">
      <button className="icon-button" type="button" onClick={onEdit} title="Bearbeiten">
        <Edit3 size={17} />
      </button>
      <button className="icon-button danger" type="button" onClick={onDelete} title="Loeschen">
        <Trash2 size={17} />
      </button>
    </td>
  );
}

function TokenList({ values }) {
  if (!values.length) {
    return <span className="muted">Keine</span>;
  }
  return (
    <div className="token-list">
      {values.map((value) => (
        <span className="token" key={value}>
          {value}
        </span>
      ))}
    </div>
  );
}

function EmptyRow({ visible, columns, label }) {
  if (!visible) {
    return null;
  }
  return (
    <tr>
      <td className="empty" colSpan={columns}>
        {label}
      </td>
    </tr>
  );
}

function formatDate(value) {
  if (!value) {
    return "-";
  }
  return new Intl.DateTimeFormat("de-CH", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

export default App;
