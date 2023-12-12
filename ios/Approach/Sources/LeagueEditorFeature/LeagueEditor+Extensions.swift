import ComposableArchitecture

extension LeagueEditor.State {
	var form: LeagueForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				new.recurrence = recurrence
				new.numberOfGames = gamesPerSeries == .static ? max(1, Int(numberOfGames)) : nil
				new.additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
				new.additionalPinfall = hasAdditionalPinfall && (new.additionalGames ?? 0) > 0 ? Int(additionalPinfall) : nil
				new.excludeFromStatistics = excludeFromStatistics
				new.location = location
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
				existing.additionalPinfall =
					hasAdditionalPinfall && (existing.additionalGames ?? 0) > 0 ? Int(additionalPinfall) : nil
				existing.excludeFromStatistics = excludeFromStatistics
				existing.location = location
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}
