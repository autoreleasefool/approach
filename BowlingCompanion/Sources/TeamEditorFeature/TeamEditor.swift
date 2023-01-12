import BaseFormFeature
import BowlersDataProviderInterface
import ComposableArchitecture
import PersistenceServiceInterface
import ResourcePickerFeature
import SharedModelsLibrary
import StringsLibrary

extension Team: BaseFormModel {
	static public var modelName: String = Strings.Team.title
}

extension Bowler: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Bowler.title : Strings.Bowler.List.title
	}
}

public struct TeamEditor: ReducerProtocol {
	public typealias Form = BaseForm<Team, Fields>

	public struct Fields: BaseFormState, Equatable {
		public let teamId: Team.ID
		public var bowlers: ResourcePicker<Bowler, Bowler.FetchRequest>.State
		@BindableState public var name = ""

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty && !bowlers.selected.isEmpty
		}

		public init(teamId: Team.ID, bowlers: [Bowler]) {
			self.teamId = teamId
			self.bowlers = .init(
				selected: Set(bowlers.map(\.id)),
				query: .init(filter: nil, ordering: .byName)
			)
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var teamMembers: TeamMembers.State
		public var isBowlerPickerPresented = false

		public init(mode: Form.Mode, bowlers: [Bowler]) {
			var fields: Fields
			switch mode {
			case let .edit(team):
				fields = .init(teamId: team.id, bowlers: bowlers)
				fields.name = team.name
				teamMembers = .init(team: team)
			case .create:
				@Dependency(\.uuid) var uuid: UUIDGenerator

				fields = .init(teamId: uuid(), bowlers: [])
				teamMembers = .init(team: nil)
			}

			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: BindableAction, Equatable {
		case setBowlerPicker(isPresented: Bool)
		case binding(BindingAction<State>)
		case form(Form.Action)
		case bowlers(ResourcePicker<Bowler, Bowler.FetchRequest>.Action)
		case teamMembers(TeamMembers.Action)
	}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createTeam,
					update: persistenceService.updateTeam,
					delete: persistenceService.deleteTeam
				))
		}

		Scope(state: \.teamMembers, action: /TeamEditor.Action.teamMembers) {
			TeamMembers()
		}

		Scope(state: \.base.form.bowlers, action: /TeamEditor.Action.bowlers) {
			ResourcePicker {
				try await bowlersDataProvider.fetchBowlers($0)
			}
		}

		Reduce { state, action in
			switch action {
			case let .form(.saveModelResult(.success(team))):
				let members = state.base.form.bowlers.selectedResources ?? state.teamMembers.bowlers.elements
				let teamMembership = TeamMembership(team: team.id, members: members)
				return .task { [teamMembership = teamMembership] in
					try await persistenceService.updateTeamMembers(teamMembership)

					return .form(.didFinishSaving)
				} catch: { error in
					return .form(.saveModelResult(.failure(error)))
				}

			case let .setBowlerPicker(isPresented):
				state.isBowlerPickerPresented = isPresented
				return .none

			case .bowlers(.saveButtonTapped), .bowlers(.cancelButtonTapped):
				state.teamMembers.bowlers = .init(
					uniqueElements: state.base.form.bowlers.selectedResources?.sorted { $0.name < $1.name } ?? []
				)
				state.isBowlerPickerPresented = false
				return .none

			case .form(.didFinishSaving):
				state.base.isLoading = false
				return .none

			case .form(.deleteModelResult(.success)):
				state.base.isLoading = false
				return .task { .form(.didFinishDeleting) }

			case .form(.deleteModelResult(.failure)), .form(.saveModelResult(.failure)):
				state.base.isLoading = false
				return .none

			case .binding, .form, .bowlers, .teamMembers:
				return .none
			}
		}
	}
}

extension TeamEditor.Fields {
	public func model(fromExisting existing: Team?) -> Team {
		return .init(
			id: teamId,
			name: name
		)
	}
}
