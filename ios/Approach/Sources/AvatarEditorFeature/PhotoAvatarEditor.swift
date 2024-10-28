import AssetsLibrary
import ComposableArchitecture
import ComposableExtensionsLibrary
import EquatablePackageLibrary
import FeatureActionLibrary
import ModelsLibrary
import Photos
import PhotosUI
import StringsLibrary
import SwiftUI

@Reducer
public struct PhotoAvatarEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var imageState: ImageState
		public var photosPickerItem: PhotosPickerItem?
		@Presents public var photoCrop: PhotoCrop.State?

		var value: Avatar.Value? {
			if let data = imageState.photoData {
				.data(data.data)
			} else {
				nil
			}
		}

		public init(avatar: Avatar.Summary?) {
			switch avatar?.value {
			case .text, .url, .none:
				self.imageState = .empty
			case let .data(data):
				self.imageState = if let image = UIImage(data: data) {
					.success(PhotoData(data: data, image: image))
				} else {
					.empty
				}
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case didStartLoadingPhoto
			case didLoadPhoto(Result<PhotoData?, Error>)
			case photoCrop(PresentationAction<PhotoCrop.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum ImageState: Equatable {
		case empty
		case loading
		case success(PhotoData)
		case failure(AlwaysEqual<Error>)

		var photoData: PhotoData? {
			switch self {
			case let .success(data): data
			case .empty, .loading, .failure: nil
			}
		}

		var isLoading: Bool {
			switch self {
			case .success, .empty, .failure: false
			case .loading: true
			}
		}
	}

	public struct PhotoData: Equatable, Sendable {
		public let data: Data
		public let image: UIImage
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		BindingReducer()
			.onChange(of: \.photosPickerItem) { _, item in
				Reduce<State, Action> { _, _ in
					.run { @MainActor send in
						guard let item else {
							send(.internal(.didLoadPhoto(.success(nil))))
							return
						}

						send(.internal(.didStartLoadingPhoto))
						await send(.internal(.didLoadPhoto(Result.of {
							try await loadTransferable(from: item)
						})))
					}
				}
			}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .doNothing:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didStartLoadingPhoto:
					state.imageState = .loading
					return .none

				case let .didLoadPhoto(.success(photoData)):
					if let photoData {
						state.photoCrop = PhotoCrop.State(image: photoData.image)
					} else {
						state.imageState = .empty
					}
					return .none

				case let .didLoadPhoto(.failure(error)):
					state.imageState = .failure(AlwaysEqual(error))
					return .none

				case let .photoCrop(.presented(.delegate(.didFinishCropping(image)))):
					guard let data = image.pngData() else {
						state.imageState = .empty
						return .none
					}

					state.imageState = .success(PhotoData(data: data, image: image))
					return .none

				case .photoCrop(.dismiss),
						.photoCrop(.presented(.view)), .photoCrop(.presented(.binding)), .photoCrop(.presented(.internal)):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$photoCrop, action: \.internal.photoCrop) {
			PhotoCrop()
		}
	}

	private func loadTransferable(from imageSelection: PhotosPickerItem) async throws -> PhotoData? {
		let avatarImage = try await imageSelection.loadTransferable(type: AvatarImage.self)
		guard let avatarImage else { return nil }
		try await Task.sleep(for: .seconds(0.5))
		return PhotoData(data: avatarImage.data, image: avatarImage.image)
	}
}

@ViewAction(for: PhotoAvatarEditor.self)
public struct PhotoAvatarEditorView: View {
	@Bindable public var store: StoreOf<PhotoAvatarEditor>

	public init(store: StoreOf<PhotoAvatarEditor>) {
		self.store = store
	}

	public var body: some View {
		Section {
			PhotosPicker(
				Strings.Avatar.Editor.Photo.choosePhoto,
				selection: $store.photosPickerItem,
				matching: .images,
				photoLibrary: .shared()
			)
		}
		.sheet(item: $store.scope(state: \.photoCrop, action: \.internal.photoCrop)) { store in
			NavigationStack {
				PhotoCropView(store: store)
			}
		}
	}
}
