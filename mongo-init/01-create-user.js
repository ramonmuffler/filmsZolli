db = db.getSiblingDB("filme_serien_db");

db.createUser({
  user: "database_user_read",
  pwd: "read_passwort",
  roles: [{ role: "read", db: "filme_serien_db" }],
});

db.createUser({
  user: "database_user_readwrite",
  pwd: "write_passwort",
  roles: [
    { role: "readWrite", db: "filme_serien_db" },
  ],
});

db.createCollection("users");
db.createCollection("contents");
db.createCollection("categories");
db.createCollection("ratings");
db.createCollection("watched");
db.createCollection("favorites");

db.contents.createIndex({ categoryIds: 1 });
db.ratings.createIndex({ contentId: 1 });
db.ratings.createIndex({ userId: 1 });
db.watched.createIndex({ contentId: 1 });
db.watched.createIndex({ userId: 1 });
db.favorites.createIndex({ contentId: 1 });
db.favorites.createIndex({ userId: 1 });
db.favorites.createIndex({ userId: 1, contentId: 1 }, { name: "userId_1_contentId_1", unique: true });
