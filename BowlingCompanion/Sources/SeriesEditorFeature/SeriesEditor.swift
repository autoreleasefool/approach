import BaseFormFeature
import ComposableArchitecture
import DateTimeLibrary
import Foundation
import PersistenceServiceInterface
import SharedModelsLibrary

extension Series: BaseFormModel {
	static public var modelName = "Series"
	public var name: String { date.longFormat }
}

public struct SeriesEditor: ReducerProtocol {
	public typealias Form = BaseForm<Series, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var leagueId: League.ID
		@BindableState public var date = Date()

		public let isDeleteable = true
		public var isSaveable = true
	}

	public struct State: Equatable {
		public var league: League
		public var base: Form.State

		public init(league: League, mode: Form.Mode) {
			self.league = league
			var fields = Fields(leagueId: league.id)
			if case let .edit(series) = mode {
				fields.date = series.date
			}

			self.base = .init(mode: mode, form: fields)
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

		Reduce { state, action in
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
			leagueId: leagueId,
			id: existing?.id ?? uuid(),
			date: date,
			// TODO: determine if there's a way to guarantee we have a league here for the # of games
			numberOfGames: existing?.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES
		)
	}
}
