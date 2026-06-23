db = db.getSiblingDB("filme_serien_db");

db.createUser({
  user: "database_user",
  pwd: "sicheres_passwort",
  roles: [
    { role: "read", db: "filme_serien_db" },
    { role: "readWrite", db: "filme_serien_db" },
  ],
});

db.createCollection("users");
db.createCollection("contents");
db.createCollection("categories");
db.createCollection("ratings");
db.createCollection("watched");

db.contents.createIndex({ categoryIds: 1 });
db.ratings.createIndex({ contentId: 1 });
db.ratings.createIndex({ userId: 1 });
db.watched.createIndex({ contentId: 1 });
db.watched.createIndex({ userId: 1 });
