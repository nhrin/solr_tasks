You can add to S3 bucket only documents with a specific structure.
It must contain 3 necessary field:
- "id"
- "user_name"
- "content"

Field "publication_date" sets automatically.

To build: mvn clean compile assembly:single