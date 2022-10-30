import BaseFormFeature
import ComposableArchitecture
import LeaguesDataProviderInterface
import SharedModelsLibrary

extension League: BaseFormModel {
	static public var modelName = "League"
}

public struct LeagueForm: ReducerProtocol {
	public typealias Form = BaseForm<League, Fields>

	public struct Fields: BaseFormState, Equatable {
		public var bowlerId: Bowler.ID
		@BindableState public var name = ""
		@BindableState public var recurrence: League.Recurrence = .repeating
		@BindableState public var gamesPerSeries: GamesPerSeries = .static
		@BindableState public var numberOfGames = League.DEFAULT_NUMBER_OF_GAMES
		@BindableState public var hasAdditionalPinfall = false
		@BindableState public var additionalPinfall = ""
		@BindableState public var additionalGames = ""

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty
		}
	}

	public struct State: Equatable {
		public var bowler: Bowler
		public var base: Form.State

		public init(bowler: Bowler, mode: Form.Mode) {
			self.bowler = bowler
			var fields = Fields(bowlerId: bowler.id)
			if case let .edit(league) = mode {
				fields.name = league.name
				fields.recurrence = league.recurrence
				fields.numberOfGames = league.numberOfGames ?? League.DEFAULT_NUMBER_OF_GAMES
				fields.gamesPerSeries = league.numberOfGames == nil ? .dynamic : .static
				fields.additionalGames = "\(league.additionalGames ?? 0)"
				fields.additionalPinfall = "\(league.additionalPinfall ?? 0)"
				fields.hasAdditionalPinfall = (league.additionalGames ?? 0) > 0
			}

			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum GamesPerSeries: String, Equatable, Identifiable, CaseIterable, Codable {
		case `static` = "Constant"
		case dynamic = "Always ask me"

		public var id: String { rawValue }
	}

	public enum Action: BindableAction, Equatable {
		case binding(BindingAction<State>)
		case form(Form.Action)
	}

	public init() {}

	@Dependency(\.uuid) var uuid
	@Dependency(\.date) var date
	@Dependency(\.leaguesDataProvider) var leaguesDataProvider

	var leagueFormService: FormModelService {
		.init(
			create: { model in
				guard let league = model as? League else { return }
				try await leaguesDataProvider.create(league)
			},
			update: { model in
				guard let league = model as? League else { return }
				try await leaguesDataProvider.update(league)
			},
			delete: { model in
				guard let league = model as? League else { return }
				try await leaguesDataProvider.delete(league)
			}
		)
	}

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.formModelService, leagueFormService)
		}

		Reduce { state, action in
			switch action {
			case .binding(\.base.form.$recurrence):
				if state.base.form.recurrence == .oneTimeEvent {
					return .task { .set(\.base.form.$gamesPerSeries, .static) }
				}
				return .none

			case .binding, .form:
				return .none
			}
		}
	}
}

extension LeagueForm.Fields {
	public func model(fromExisting existing: League?) -> League {
		@Dependency(\.uuid) var uuid: UUIDGenerator
		@Dependency(\.date) var date: DateGenerator

		let numberOfGames = gamesPerSeries == .static ? self.numberOfGames : nil
		let additionalGames = hasAdditionalPinfall ? Int(additionalGames) : nil
		let additionalPinfall: Int?
		if let additionalGames {
			additionalPinfall = hasAdditionalPinfall && additionalGames > 0 ? Int(self.additionalPinfall) : nil
		} else {
			additionalPinfall = nil
		}

		return .init(
			bowlerId: bowlerId,
			id: existing?.id ?? uuid(),
			name: name,
			recurrence: recurrence,
			numberOfGames: numberOfGames,
			additionalPinfall: additionalPinfall,
			additionalGames: additionalGames,
			createdAt: existing?.createdAt ?? date(),
			lastModifiedAt: date()
		)
	}
}
