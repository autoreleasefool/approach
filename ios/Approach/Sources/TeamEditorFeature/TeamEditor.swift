import ComposableArchitecture
import FeatureActionLibrary
import FormFeature
import Foundation
import ModelsLibrary
import StringsLibrary
import TeamsRepositoryInterface

public typealias TeamForm = FormFeature.Form<Team.Create, Team.Edit>

@Reducer
public struct TeamEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var name: String
		public var teamMembers: IdentifiedArrayOf<Bowler.Summary>

		public let initialValue: TeamForm.Value
		public var form: TeamForm.State

		@Presents public var destination: Destination.State?

		public init(value: InitialValue) {
			switch value {
			case let .create(new):
				self.name = new.name
				self.teamMembers = []
				self.initialValue = .create(new)
			case let .edit(existing):
				self.name = existing.team.name
				self.teamMembers = .init(uniqueElements: existing.members)
				self.initialValue = .edit(existing.team)
			}

			self.form = .init(initialValue: self.initialValue)
		}

		mutating func syncFormSharedState() {
			switch form.initialValue {
			case var .create(new):
				new.name = name
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				form.value = .edit(existing)
			}
		}

		var teamId: Team.ID {
			switch initialValue {
			case let .create(create): create.id
			case let .edit(edit): edit.id
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapManageBowlers
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case form(TeamForm.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer(state: .equatable)
	public enum Destination {}

	public enum InitialValue {
		case create(Team.Create)
		case edit(Team.EditWithMembers)
	}

	public init() {}

	@Dependency(TeamsRepository.self) var teams
	@Dependency(\.dismiss) var dismiss

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.form, action: \.internal.form) {
			TeamForm()
//				.dependency(RecordPersistence(
//					
//				))
		}

		Reduce<State, Action> { state, action in
			return .none
		}
		.ifLet(\.$destination, action: \.internal.destination)
	}
}

extension Team.Create: CreateableRecord {
	public static let modelName = Strings.Team.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension Team.Edit: EditableRecord {
	public var isSaveable: Bool {
		!name.isEmpty
	}

	public var isDeleteable: Bool { false }
	public var isArchivable: Bool { true }
}
