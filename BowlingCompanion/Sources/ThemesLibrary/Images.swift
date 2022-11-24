import SwiftUI

extension Theme {
	public enum Images {}
}

extension Theme.Images {
	public enum EmptyState {
		public static let alleys = UIImage(named: "Images/EmptyState/Alleys")!
		public static let bowlers = UIImage(named: "Images/EmptyState/Bowlers")!
		public static let games = UIImage(named: "Images/EmptyState/Games")!
		public static let gear = UIImage(named: "Images/EmptyState/Gear")!
		public static let leagues = UIImage(named: "Images/EmptyState/Leagues")!
		public static let series = UIImage(named: "Images/EmptyState/Series")!
	}

	public enum Error {
		public static let notFound = UIImage(named: "Images/Error/NotFound")!
	}
}
