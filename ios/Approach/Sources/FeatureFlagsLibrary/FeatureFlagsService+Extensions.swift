import FeatureFlagsPackageLibrary
import FeatureFlagsPackageServiceInterface

extension FeatureFlagsService {
	public func isFlagEnabled(_ flag: FeatureFlag) -> Bool {
		(try? self.isEnabled(flag)) ?? false
	}

	public func allFlagsEnabled(_ flags: [FeatureFlag]) -> Bool {
		(try? self.allEnabled(flags)) ?? false
	}
}
