import BaseFormFeature
import ComposableArchitecture
import DateTimeLibrary
import Foundation
import PersistenceServiceInterface
import SharedModelsLibrary
import StringsLibrary

extension Series: BaseFormModel {
	static public var modelName = Strings.Series.Model.name
	public var name: String { date.longFormat }
}

public struct SeriesEditor: ReducerProtocol {
	public typealias Form = BaseForm<Series, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var league: League
		@BindableState public var numberOfGames: Int
		@BindableState public var date = Date()

		public let isDeleteable = true
		public var isSaveable = true
	}

	public struct State: Equatable {
		public var base: Form.State
		public let hasAlleysEnabled: Bool

		public init(
			league: League,
			mode: Form.Mode,
			date: Date,
			hasAlleysEnabled: Bool
		) {
			var fields = Fields(league: league, date: date)
			if case let .edit(series) = mode {
				fields.date = series.date
				fields.numberOfGames = series.numberOfGames
			}

			self.base = .init(mode: mode, form: fields)
			self.hasAlleysEnabled = hasAlleysEnabled
		}
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case form(Form.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.persistenceService) var persistenceService

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createSeries,
					update: persistenceService.updateSeries,
					delete: persistenceService.deleteSeries
				))
		}

		Reduce { _, action in
			switch action {
			case .binding:
				return .none

			case .form:
				return .none
			}
		}
	}
}

extension SeriesEditor.Fields {
	public func model(fromExisting existing: Series?) -> Series {
		@Dependency(\.uuid) var uuid: UUIDGenerator

		return .init(
			league: league.id,
			id: existing?.id ?? uuid(),
			date: date,
			numberOfGames: existing?.numberOfGames ?? league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES,
			alley: alleyPicker.selected.first
		)
	}
}
