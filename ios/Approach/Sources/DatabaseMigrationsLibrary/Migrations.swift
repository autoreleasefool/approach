import GRDB
import GRDBDatabasePackageLibrary

public enum Migrations {
	public static var approachMigrations: [Migration.Type] {
		[
			Migration20230325CreateBowler.self,
			Migration20230408CreateAlley.self,
			Migration20230408CreateLeague.self,
			Migration20230409CreateSeries.self,
			Migration20230413CreateGear.self,
			Migration20230414CreateGame.self,
			Migration20230414CreateFrame.self,
			Migration20230415CreateLane.self,
			Migration20230417CreateSeriesLanePivot.self,
			Migration20230425CreateAvatar.self,
			Migration20230426CreateLocation.self,
			Migration20230506AddGearToFrame.self,
			Migration20230514CreateMatchPlay.self,
			Migration20230519AddScoreToGame.self,
			Migration20230531GameLanes.self,
			Migration20230602AddGearToGame.self,
			Migration20230630CreateStatisticsWidget.self,
			Migration20230912CreateBowlerPreferredGear.self,
			Migration20230913AddAvatarToGear.self,
			Migration20230918ChangeWidgetSourceToColumns.self,
			Migration20230918OnDeleteGearBowlerSetNull.self,
			Migration20230918MigrateStatisticsWidgetType.self,
			Migration20230920ValidateGameScores.self,
			Migration20231002ValidateGameScores.self,
			Migration20231022IsArchivedProperty.self,
			Migration20231022DropSeriesNumberOfGames.self,
			Migration20231023AddArchivePropertyToGame.self,
			Migration20231024IsArchivedToArchivedOn.self,
			Migration20231128AddDurationToGame.self,
			Migration20231220MigrateRecurringLeagues.self,
			Migration20231220RenameLeagueNumberOfGames.self,
			Migration20240322AddBowledOnDateToSeries.self,
			Migration20240329ClearExtraRolledBalls.self,
			Migration20241021CreateTeam.self,
			Migration20250315CreateAchievements.self,
		]
	}
}
