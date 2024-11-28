import pymysql
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

def recommend_articles(username):
    # Connect to the MySQL database
    connection = pymysql.connect(
        host='localhost',
        user='root',
        password='',
        database='personalizedArticles'
    )

    try:
        # Fetch user's reading history
        user_history_query = """
            SELECT content FROM UserArticleHistory 
            INNER JOIN Articles ON UserArticleHistory.articleTitle = Articles.title
            WHERE username = %s AND likeDislikeStatus = 1
        """
        user_history_df = pd.read_sql(user_history_query, connection, params=[username])

        # Fetch all articles
        articles_query = "SELECT title, description, content, category FROM Articles"
        articles_df = pd.read_sql(articles_query, connection)

        # Combine article content for similarity
        vectorizer = TfidfVectorizer(stop_words='english')
        tfidf_matrix = vectorizer.fit_transform(articles_df['content'])

        # Calculate cosine similarity between user history and all articles
        recommendations = []
        for user_article in user_history_df['content']:
            user_vector = vectorizer.transform([user_article])
            similarity_scores = cosine_similarity(user_vector, tfidf_matrix)
            similar_articles = articles_df.iloc[similarity_scores.argsort()[0][-5:]]  # Top 5 recommendations
            recommendations.extend(similar_articles.to_dict('records'))

        return recommendations

    finally:
        connection.close()

if __name__ == "__main__":
    import sys
    username = sys.argv[1]  # Get username from command-line arguments
    recommendations = recommend_articles(username)
    for rec in recommendations:
        print(f"{rec['title']},{rec['description']},{rec['category']}")
