import Foundation
import GRDB

struct Migration20240329ClearExtraRolledBalls: DBMigration {
	static func migrate(_ db: Database) throws {
		do {
			try db.execute(sql:
"""
WITH intRoll AS (
	SELECT
		frame.'index' AS frameIndex,
		frame.gameId AS gameId,
		CAST(SUBSTR(frame.roll0, 2) AS INTEGER) AS roll0,
		CAST(SUBSTR(frame.roll1, 2) AS INTEGER) AS roll1
	FROM frame
	WHERE frame.'index' < 9
),

clear AS (
	SELECT
		frame.'index' AS frameIndex,
		frame.gameId AS gameId,
		intRoll.roll0 = 11111 AS roll0,
		intRoll.roll0 + intRoll.roll1 = 11111 AS roll1
	FROM frame
	INNER JOIN intRoll ON frame.'index' = intRoll.frameIndex AND frame.gameId = intRoll.gameId
)

UPDATE
	frame
SET
	ball1 = NULL
WHERE (frame.gameId, frame.'index') IN (
	SELECT
		frame.gameId AS gameId,
		frame.'index' AS frameIndex
	FROM frame
	INNER JOIN clear ON frame.'index' = clear.frameIndex AND frame.gameId = clear.gameId
	WHERE clear.roll0 AND frame.ball1 IS NOT NULL
)
"""
			)

			try db.execute(sql:
"""
WITH intRoll AS (
	SELECT
		frame.'index' AS frameIndex,
		frame.gameId AS gameId,
		CAST(SUBSTR(frame.roll0, 2) AS INTEGER) AS roll0,
		CAST(SUBSTR(frame.roll1, 2) AS INTEGER) AS roll1
	FROM frame
	WHERE frame.'index' < 9
),

clear AS (
	SELECT
		frame.'index' AS frameIndex,
		frame.gameId AS gameId,
		intRoll.roll0 = 11111 AS roll0,
		intRoll.roll0 + intRoll.roll1 = 11111 AS roll1
	FROM frame
	INNER JOIN intRoll ON frame.'index' = intRoll.frameIndex AND frame.gameId = intRoll.gameId
)

UPDATE
	frame
SET
	ball2 = NULL
WHERE (frame.gameId, frame.'index') IN (
	SELECT
		frame.gameId AS gameId,
		frame.'index' AS frameIndex
	FROM frame
	INNER JOIN clear ON frame.'index' = clear.frameIndex AND frame.gameId = clear.gameId
	WHERE (clear.roll0 OR clear.roll1) AND frame.ball2 IS NOT NULL
)
"""
			)
		} catch {
			// TODO: Report error to sentry
		}
	}
}
